import {Component, inject, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {RecipeCardComponent} from "../../shared/components/recipe-card/recipe-card.component";
import {RecipeShortDto} from '../../data/interfaces/recipe.interface';
import {RecipeService} from '../../data/services/recipe.service';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-favorites-page',
  standalone: true,
    imports: [
        NgForOf,
        RecipeCardComponent,
    ],
  templateUrl: './favorites-page.component.html',
  styleUrl: './favorites-page.component.scss'
})
export class FavoritesPageComponent implements OnInit {
  recipes: RecipeShortDto[] = [];

  private recipeService = inject(RecipeService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    const authorId = this.authService.currentUser?.id;
    if (!authorId) return;

    this.recipeService.getFavorites().subscribe(data => {
      this.recipes = data;
    });
  }
}
