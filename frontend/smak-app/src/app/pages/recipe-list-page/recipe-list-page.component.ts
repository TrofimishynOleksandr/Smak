import {Component, inject, OnInit, signal} from '@angular/core';
import {RecipeShortDto} from '../../data/interfaces/recipe.interface';
import {CategoryDto} from '../../data/interfaces/category.interface';
import {RecipeService} from '../../data/services/recipe.service';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {RecipeCardComponent} from '../../shared/components/recipe-card/recipe-card.component';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs';

@Component({
  selector: 'app-recipe-list-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgForOf,
    RecipeCardComponent
  ],
  templateUrl: './recipe-list-page.component.html',
  styleUrl: './recipe-list-page.component.scss'
})
export class RecipeListPageComponent implements OnInit {
  recipeService = inject(RecipeService);
  fb = inject(FormBuilder);
  router = inject(Router);
  route = inject(ActivatedRoute);

  recipes = signal<RecipeShortDto[]>([]);
  categories = signal<CategoryDto[]>([]);

  filters!: FormGroup<{
    search: FormControl<string | null>;
    categoryId: FormControl<string | null>;
    maxCookTime: FormControl<number | null>;
    minRating: FormControl<number | null>;
    sortBy: FormControl<string | null>;
  }>;

  ngOnInit(): void {
    this.filters = this.fb.group({
      search: this.fb.control<string | null>(null),
      categoryId: this.fb.control<string | null>(null),
      maxCookTime: this.fb.control<number | null>(null),
      minRating: this.fb.control<number | null>(null),
      sortBy: this.fb.control<string | null>(null)
    });

    this.recipeService.getCategories().subscribe(c => this.categories.set(c));

    this.route.queryParams.subscribe(params => {
      this.filters.patchValue({
        search: params['search'] ?? null,
        categoryId: params['categoryId'] ?? null,
        maxCookTime: params['maxCookTime'] ?? null,
        minRating: params['minRating'] ?? null,
        sortBy: params['sortBy'] ?? null
      }, { emitEvent: false });

      this.submitSearch();
    });

    this.filters.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        map(val => this.cleanQuery(val)),
        filter(() => this.filters.valid)
      )
      .subscribe(cleaned => {
        this.updateQueryParams(cleaned);
        this.recipeService.searchRecipes(cleaned).subscribe(data => this.recipes.set(data));
      });
  }

  updateQueryParams(cleaned: any) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: cleaned
    });
  }

  submitSearch(): void {
    const query = this.cleanQuery(this.filters.value);
    this.recipeService.searchRecipes(query).subscribe(data => this.recipes.set(data));
  }

  private cleanQuery(obj: any): Record<string, any> {
    return Object.fromEntries(
      Object.entries(obj).filter(([_, v]) => v !== null && v !== '')
    );
  }
}
