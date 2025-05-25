import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {IngredientDto, UnitDto} from '../../../data/interfaces/ingredient.interface';
import {CdkDrag, CdkDragHandle} from '@angular/cdk/drag-drop';
import {NgForOf, NgIf, NgStyle} from '@angular/common';
import {NgOptionTemplateDirective, NgSelectComponent} from '@ng-select/ng-select';

@Component({
  selector: 'app-ingredient-item',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CdkDrag,
    CdkDragHandle,
    NgForOf,
    NgSelectComponent,
    NgStyle,
    NgOptionTemplateDirective,
    NgIf,
  ],
  templateUrl: './ingredient-item.component.html',
  styleUrl: './ingredient-item.component.scss'
})
export class IngredientItemComponent {
  @Input() group!: FormGroup;
  @Input() units: UnitDto[] = [];
  @Input() ingredients: IngredientDto[] = [];
  @Output() remove = new EventEmitter<void>();

  isCustomSelected(): boolean {
    const name = this.group.get('name')?.value;
    return !!this.ingredients.find(i => i.name === name && i.isCustom);
  }

  trackByName = (index: number, item: IngredientDto) => item?.name ?? index;
}
