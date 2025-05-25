import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import { RouterModule } from '@angular/router';
import { CategoryImageUrlPipe } from '../../../shared/pipes/category-image-url.pipe';
import {CategoryDto} from '../../../data/interfaces/category.interface';
import {SvgIconComponent} from '../../../shared/svg-icon/svg-icon.component';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-categories-slider',
  standalone: true,
  imports: [RouterModule, SvgIconComponent, NgForOf, CategoryImageUrlPipe],
  templateUrl: './categories-slider.component.html',
  styleUrl: './categories-slider.component.scss',
})
export class CategoriesSliderComponent {
  @Input() categories: CategoryDto[] = [];
  @ViewChild('scrollContainer') scrollContainer!: ElementRef<HTMLDivElement>;

  scroll(direction: 'left' | 'right') {
    const el = this.scrollContainer.nativeElement;
    el.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }
}
