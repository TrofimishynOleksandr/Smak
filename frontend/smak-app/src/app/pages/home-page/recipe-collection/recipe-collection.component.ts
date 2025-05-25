import {Component, Input} from '@angular/core';
import {NgForOf} from '@angular/common';
import {RecipeCardComponent} from '../../../shared/components/recipe-card/recipe-card.component';
import {SvgIconComponent} from '../../../shared/svg-icon/svg-icon.component';
import {RecipeShortDto} from '../../../data/interfaces/recipe.interface';

@Component({
  selector: 'app-recipe-collection',
  standalone: true,
  imports: [NgForOf, RecipeCardComponent, SvgIconComponent],
  templateUrl: './recipe-collection.component.html',
  styleUrl: './recipe-collection.component.scss'
})
export class RecipeCollectionComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) recipes: RecipeShortDto[] = [];

  scroll(direction: 'left' | 'right') {
    const el = document.getElementById(this.title);
    if (!el) return;
    el.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth'
    });
  }
}
