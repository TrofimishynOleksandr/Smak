import {Component, inject, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {RecipeCardComponent} from "../../shared/components/recipe-card/recipe-card.component";
import {RouterLink} from "@angular/router";
import {RecipeShortDto} from '../../data/interfaces/recipe.interface';
import {RecipeService} from '../../data/services/recipe.service';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-my-recipes-page',
  standalone: true,
    imports: [
        NgForOf,
        RecipeCardComponent,
        RouterLink
    ],
  templateUrl: './my-recipes-page.component.html',
  styleUrl: './my-recipes-page.component.scss'
})
export class MyRecipesPageComponent implements OnInit {
  recipes: RecipeShortDto[] = [];

  private recipeService = inject(RecipeService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const authorId = this.authService.currentUser?.id;
    if (!authorId) return;

    this.recipeService.searchRecipes({ authorId }).subscribe(data => {
      this.recipes = data;
    });
  }
}
