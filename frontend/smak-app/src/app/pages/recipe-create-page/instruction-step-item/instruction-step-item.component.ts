import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {CdkDrag, CdkDragHandle} from '@angular/cdk/drag-drop';
import {NgIf} from '@angular/common';
import {SvgIconComponent} from '../../../shared/svg-icon/svg-icon.component';

@Component({
  selector: 'app-instruction-step-item',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CdkDrag,
    CdkDragHandle,
    NgIf,
    SvgIconComponent
  ],
  templateUrl: './instruction-step-item.component.html',
  styleUrl: './instruction-step-item.component.scss'
})
export class InstructionStepItemComponent {
  @Input() group!: FormGroup;
  @Output() remove = new EventEmitter<void>();
  @Output() imageChange = new EventEmitter<File>();
  @Input() index!: number;
  imagePreview: string | null = null;

  onImageChange(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.group.get('image')?.setValue(file);

      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }
}
