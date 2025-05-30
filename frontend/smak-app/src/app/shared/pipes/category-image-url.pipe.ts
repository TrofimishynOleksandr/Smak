import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'categoryImageUrl',
  standalone: true
})
export class CategoryImageUrlPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '/assets/images/category-placeholder.png';
    return `http://192.168.0.103:5127${value}`;
  }
}
