import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { RecipeShortDto } from '../../../data/interfaces/recipe.interface';
import { RecipeService } from '../../../data/services/recipe.service';
import { RecipeImageUrlPipe } from '../../pipes/recipe-image-url.pipe';
import {NgClass, NgForOf} from '@angular/common';
import { AuthService } from '../../../auth/auth.service';
import {SvgIconComponent} from '../../svg-icon/svg-icon.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-recipe-card',
  standalone: true,
  imports: [RecipeImageUrlPipe, NgForOf, SvgIconComponent, NgClass, RouterLink],
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.scss'
})
export class RecipeCardComponent implements OnInit {
  @Input({ required: true }) recipe!: RecipeShortDto;

  isFavorite = signal(false);

  authService = inject(AuthService);
  recipeService = inject(RecipeService);

  ngOnInit(): void {
    this.isFavorite.set(this.recipe.isFavorite);
  }

  toggleFavorite(): void {
    if (!this.authService.isAuth) {
      this.authService.showLoginModal();
      return;
    }

    const isNowFavorite = !this.isFavorite();
    this.isFavorite.set(isNowFavorite);

    this.recipeService.toggleFavorite(this.recipe.id, !isNowFavorite).subscribe(() => {
      this.recipe.isFavorite = isNowFavorite;
    });
  }

  get trimmedDescription(): string {
    const maxLength = 100;
    return this.recipe.description.length > maxLength
      ? this.recipe.description.slice(0, maxLength) + '...'
      : this.recipe.description;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/assets/images/recipe-placeholder.jpg';
  }

  get starFillArray(): boolean[] {
    const rounded = Math.round(this.recipe.averageRating);
    return Array.from({ length: 5 }, (_, i) => i < rounded);
  }

  goToRecipe(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (target.closest('.recipe-card__like')) {
      event.stopPropagation();
      return;
    }
  }

}
