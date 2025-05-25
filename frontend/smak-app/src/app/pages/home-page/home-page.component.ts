import {Component, inject, OnInit} from '@angular/core';
import { RecipeShortDto } from '../../data/interfaces/recipe.interface';
import { RecipeService } from '../../data/services/recipe.service';
import {NgForOf} from '@angular/common';
import {AuthService} from '../../auth/auth.service';
import {CategoriesSliderComponent} from './categories-slider/categories-slider.component';
import {ChefsSliderComponent} from './chefs-slider/chefs-slider.component';
import {CategoryDto} from '../../data/interfaces/category.interface';
import {ChefDto} from '../../data/interfaces/user.interface';
import {RecipeCollectionComponent} from './recipe-collection/recipe-collection.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    NgForOf,
    CategoriesSliderComponent,
    ChefsSliderComponent,
    RecipeCollectionComponent
  ],
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit {
  collections: { title: string; recipes: RecipeShortDto[] }[] = [];
  categories: CategoryDto[] = [];
  chefs: ChefDto[] = [];

  recipeService = inject(RecipeService)
  authService = inject(AuthService)

  ngOnInit(): void {
    this.loadCategories();
    this.loadCollections();
    this.loadChefs();

    this.authService.loginSuccess$.subscribe(() => this.loadCollections());
    this.authService.logout$.subscribe(() => this.loadCollections());
  }

  loadCategories() {
    this.recipeService.getCategories().subscribe(data => {
      this.categories = data;
    });
  }

  loadCollections() {
    this.recipeService.getCollections().subscribe(data => {
      this.collections = data;
    });
  }

  loadChefs() {
    this.recipeService.getChefs().subscribe(data => {
      this.chefs = data;
    });
  }
}
