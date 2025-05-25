import {Component, inject, OnInit} from '@angular/core';
import {RecipeShortDto} from '../../data/interfaces/recipe.interface';
import {ActivatedRoute} from '@angular/router';
import {RecipeService} from '../../data/services/recipe.service';
import {RecipeCardComponent} from '../../shared/components/recipe-card/recipe-card.component';
import {AvatarUrlPipe} from '../../shared/pipes/avatar-url.pipe';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-chef-profile-page',
  standalone: true,
  imports: [
    RecipeCardComponent,
    AvatarUrlPipe,
    NgForOf
  ],
  templateUrl: './chef-profile-page.component.html',
  styleUrl: './chef-profile-page.component.scss'
})
export class ChefProfilePageComponent implements OnInit {
  recipes: RecipeShortDto[] = [];
  chefId: string = '';
  chefName: string = '';
  avatarUrl?: string = '';

  route = inject(ActivatedRoute);
  recipeService = inject(RecipeService);

  ngOnInit(): void {
    this.chefId = this.route.snapshot.params['id'];

    this.recipeService.getChefById(this.chefId).subscribe(chef => {
      this.chefName = chef.name;
      this.avatarUrl = chef.avatarUrl;
    });

    this.recipeService.searchRecipes({ authorId: this.chefId }).subscribe(r => {
      this.recipes = r;
    });
  }
}
