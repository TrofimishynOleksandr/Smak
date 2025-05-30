import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'recipeImageUrl',
  standalone: true
})
export class RecipeImageUrlPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '/assets/images/recipe-placeholder.jpg';
    return `http://192.168.0.103:5127${value}`;
  }
}
