import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {RecipeDetailsDto, RecipeShortDto} from '../interfaces/recipe.interface';
import {CategoryDto} from '../interfaces/category.interface';
import {ChefDto} from '../interfaces/user.interface';
import {IngredientDto, UnitDto} from '../interfaces/ingredient.interface';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private api = 'http://localhost:5127/api';
  httpClient = inject(HttpClient);

  getCollections() {
    return this.httpClient.get<{ title: string; recipes: RecipeShortDto[] }[]>(`${this.api}/recipe/collections`);
  }

  getFavorites() {
    return this.httpClient.get<RecipeShortDto[]>(`${this.api}/recipe/favorites`);
  }

  getRecipeById(id: string) {
    return this.httpClient.get<RecipeDetailsDto>(`${this.api}/recipe/${id}`);
  }

  getCategories() {
    return this.httpClient.get<CategoryDto[]>(`${this.api}/category`);
  }

  getChefs() {
    return this.httpClient.get<ChefDto[]>(`${this.api}/user/popular-chefs`);
  }

  getChefById(chefId: string) {
    return this.httpClient.get<ChefDto>(`${this.api}/user/${chefId}`);
  }

  toggleFavorite(recipeId: string, currentlyFavorite: boolean) {
    const url = `${this.api}/recipe/${recipeId}/favorite`;
    return currentlyFavorite
      ? this.httpClient.delete<void>(url)
      : this.httpClient.post<void>(url, null);
  }

  searchRecipes(query: any) {
    const params = new HttpParams({ fromObject: query });
    return this.httpClient.get<RecipeShortDto[]>(`${this.api}/recipe/search`, { params });
  }

  createRecipe(formData: FormData) {
    return this.httpClient.post<void>(`${this.api}/recipe`, formData);
  }

  deleteRecipe(id: string) {
    return this.httpClient.delete(`${this.api}/recipe/${id}`);
  }

  getAllIngredients() {
    return this.httpClient.get<IngredientDto[]>(`${this.api}/ingredient`);
  }

  getUnitsOfMeasure() {
    return this.httpClient.get<UnitDto[]>(`${this.api}/ingredient/units`);
  }

  addReview(dto: { recipeId: string, rating: number, comment: string }) {
    return this.httpClient.post(`${this.api}/recipe/${dto.recipeId}/review`, dto);
  }

  deleteReview(id: string) {
    return this.httpClient.delete(`${this.api}/review/${id}`);
  }
}
