import {Component, Input} from '@angular/core';
import {NgClass} from '@angular/common';

@Component({
  selector: 'svg[icon]',
  standalone: true,
  imports: [
    NgClass
  ],
  template: `
    <svg [attr.width]="size" [attr.height]="size" class="icon16" [ngClass]="extraClass" xmlns="http://www.w3.org/2000/svg">
      <use [attr.href]="href"></use>
    </svg>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
    svg {
      fill: currentColor;
      stroke: currentColor;
    }
  `]
})
export class SvgIconComponent {
  @Input() icon = '';
  @Input() size = '16';
  @Input() extraClass = '';

  get href() {
    return `/assets/svg/${this.icon}.svg#${this.icon}`;
  }
}
