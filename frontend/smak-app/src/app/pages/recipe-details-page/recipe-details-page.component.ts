import {Component, inject, OnInit, signal} from '@angular/core';
import {SvgIconComponent} from '../../shared/svg-icon/svg-icon.component';
import {CommonModule, NgForOf, NgIf} from '@angular/common';
import {RecipeImageUrlPipe} from '../../shared/pipes/recipe-image-url.pipe';
import {RecipeService} from '../../data/services/recipe.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {RecipeDetailsDto} from '../../data/interfaces/recipe.interface';
import {StepImageUrlPipe} from '../../shared/pipes/step-image-url.pipe';
import {AuthService} from '../../auth/auth.service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-recipe-details-page',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgForOf,
    SvgIconComponent,
    RecipeImageUrlPipe,
    SvgIconComponent,
    StepImageUrlPipe,
    RouterLink,
    ReactiveFormsModule
  ],
  templateUrl: './recipe-details-page.component.html',
  styleUrl: './recipe-details-page.component.scss'
})
export class RecipeDetailsPageComponent implements OnInit {
  recipeId: string = '';
  recipe?: RecipeDetailsDto;

  isFavorite = signal(false);

  authService = inject(AuthService);
  recipeService = inject(RecipeService);
  route = inject(ActivatedRoute);

  currentUserId = this.authService.currentUser?.id;

  isOwner = false;
  router = inject(Router);

  fb = inject(FormBuilder);

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.recipeId = params['id'];
      this.recipeService.getRecipeById(this.recipeId).subscribe(data => {
        this.recipe = data;
        this.isFavorite.set(data.isFavorite);

        const token = this.authService.token;
        if (token) {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const userId = payload["http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier"];
          this.isOwner = userId === data.authorId;
        }
      });
    });
  }

  toggleFavorite(): void {
    if (!this.authService.isAuth) {
      this.authService.showLoginModal();
      return;
    }

    const isNowFavorite = !this.isFavorite();
    this.isFavorite.set(isNowFavorite);

    this.recipeService.toggleFavorite(this.recipe!.id, !isNowFavorite).subscribe(() => {
      this.recipe!.isFavorite = isNowFavorite;
    });
  }

  get starFillArray(): boolean[] {
    const rounded = Math.round(this.recipe?.averageRating ?? 0);
    return Array.from({ length: 5 }, (_, i) => i < rounded);
  }

  deleteRecipe() {
    if (confirm('Ви впевнені, що хочете видалити цей рецепт?')) {
      this.recipeService.deleteRecipe(this.recipeId).subscribe(() => {
        this.router.navigate(['/'])
      });
    }
  }

  reviewForm = this.fb.group({
    rating: [5, Validators.required],
    comment: ['']
  });

  get canReview(): boolean {
    const currentId = this.authService.currentUser?.id;
    return currentId !== this.recipe?.authorId &&
      !this.recipe?.reviews?.some(r => r.authorId === currentId);
  }

  submitReview() {
    if (!this.recipe) return;

    const payload = {
      recipeId: this.recipe.id,
      rating: this.reviewForm.value.rating!,
      comment: this.reviewForm.value.comment!
    };

    this.recipeService.addReview(payload).subscribe(() => {
      this.recipeService.getRecipeById(this.recipe!.id).subscribe(r => {
        this.recipe = r;
        this.isFavorite.set(r.isFavorite);
        this.reviewForm.reset({ rating: 5, comment: '' });
      });
    });
  }

  deleteReview(): void {
    if (!this.recipe) return;
    this.recipeService.deleteReview(this.recipe.id).subscribe(() => {
      this.recipeService.getRecipeById(this.recipeId).subscribe(data => {
        this.recipe = data;
        this.isFavorite.set(this.recipe.isFavorite);
      });
    });
  }

}
