import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {AvatarUrlPipe} from '../../../shared/pipes/avatar-url.pipe';
import {ChefDto} from '../../../data/interfaces/user.interface';
import {SvgIconComponent} from '../../../shared/svg-icon/svg-icon.component';

@Component({
  selector: 'app-chefs-slider',
  standalone: true,
  imports: [CommonModule, RouterModule, AvatarUrlPipe, SvgIconComponent],
  templateUrl: './chefs-slider.component.html',
  styleUrl: './chefs-slider.component.scss'
})
export class ChefsSliderComponent {
  @Input() chefs: ChefDto[] = [];
  @ViewChild('scrollContainer') scrollContainer!: ElementRef<HTMLDivElement>;

  scroll(direction: 'left' | 'right') {
    const el = this.scrollContainer.nativeElement;
    el.scrollBy({
      left: direction === 'right' ? 300 : -300,
      behavior: 'smooth',
    });
  }
}
