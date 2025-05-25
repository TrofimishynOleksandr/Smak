import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {RecipeService} from '../../data/services/recipe.service';
import {NgForOf, NgIf} from '@angular/common';
import {
  CdkDragDrop, DragDropModule,
  moveItemInArray
} from '@angular/cdk/drag-drop';
import {IngredientDto, UnitDto} from '../../data/interfaces/ingredient.interface';
import {CategoryDto} from '../../data/interfaces/category.interface';
import {IngredientItemComponent} from './ingredient-item/ingredient-item.component';
import {InstructionStepItemComponent} from './instruction-step-item/instruction-step-item.component';
import {Router} from '@angular/router';
import {SvgIconComponent} from '../../shared/svg-icon/svg-icon.component';

@Component({
  selector: 'app-recipe-create-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgForOf,
    FormsModule,
    DragDropModule,
    IngredientItemComponent,
    InstructionStepItemComponent,
    NgIf,
    SvgIconComponent
  ],
  templateUrl: './recipe-create-page.component.html',
  styleUrl: './recipe-create-page.component.scss'
})
export class RecipeCreatePageComponent implements OnInit {
  recipeService = inject(RecipeService);
  fb = inject(FormBuilder);
  router = inject(Router);

  allIngredients: WritableSignal<IngredientDto[]> = signal([]);
  categories: WritableSignal<CategoryDto[]> = signal([]);
  units: WritableSignal<UnitDto[]> = signal<UnitDto[]>([]);

  imagePreview: string | null = null;

  form!: FormGroup;

  ngOnInit(): void {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(1000)]],
      cookTimeMinutes: [0, Validators.required],
      categoryId: [null, Validators.required],
      image: [null],
      ingredients: this.fb.array([]),
      instructions: this.fb.array([])
    });

    this.loadIngredients();
    this.loadCategories();
    this.loadUnits();
    this.addIngredient();
    this.addStep();
  }

  get ingredients(): FormArray {
    return this.form.get('ingredients') as FormArray;
  }

  get instructions(): FormArray {
    return this.form.get('instructions') as FormArray;
  }

  addIngredient(): void {
    const group = this.fb.group({
      name: ['', Validators.required],
      quantity: [null],
      unit: [null]
    });
    this.ingredients.push(group);
  }

  addStep(): void {
    const group = this.fb.group({
      stepNumber: [this.instructions.length + 1],
      description: ['', Validators.required],
      image: [null]
    });
    this.instructions.push(group);
  }

  removeIngredient(i: number) {
    this.ingredients.removeAt(i);
  }

  removeStep(i: number) {
    this.instructions.removeAt(i);
  }

  onFileChange(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.form.get('image')?.setValue(file);
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  reorderIngredients(event: CdkDragDrop<AbstractControl[]>) {
    moveItemInArray(this.ingredients.controls, event.previousIndex, event.currentIndex);
  }

  reorderSteps(event: CdkDragDrop<AbstractControl[]>) {
    moveItemInArray(this.instructions.controls, event.previousIndex, event.currentIndex);
    this.instructions.controls.forEach((control, index) => {
      control.get('stepNumber')?.setValue(index + 1);
    });
  }

  submit(): void {
    if (this.form.invalid) return;

    const formData = new FormData();
    const value = this.form.value;

    formData.append('title', value.title);
    formData.append('description', value.description);
    formData.append('cookTimeMinutes', value.cookTimeMinutes.toString());
    formData.append('categoryId', value.categoryId);

    if (value.image) {
      formData.append('image', value.image);
    }

    value.ingredients.forEach((i: any, idx: number) => {
      formData.append(`ingredients[${idx}].name`, i.name);
      if (i.quantity !== null) {
        formData.append(`ingredients[${idx}].quantity`, i.quantity);
      }
      if (i.unit !== null) {
        formData.append(`ingredients[${idx}].unit`, i.unit);
      }
    });

    value.instructions.forEach((s: any, idx: number) => {
      formData.append(`instructions[${idx}].stepNumber`, s.stepNumber);
      formData.append(`instructions[${idx}].description`, s.description);
      if (s.image) {
        formData.append(`instructions[${idx}].image`, s.image);
      }
    });

    this.recipeService.createRecipe(formData).subscribe({
      next: () => {
        this.form.reset();
        this.ingredients.clear();
        this.instructions.clear();
        this.addIngredient();
        this.addStep();
        this.router.navigate(['/my-recipes']);
      }
    });
  }

  private loadIngredients() {
    this.recipeService.getAllIngredients().subscribe(data => {
      this.allIngredients.set(data);
    });
  }

  private loadCategories() {
    this.recipeService.getCategories().subscribe(data => {
      this.categories.set(data);
    });
  }

  private loadUnits() {
    this.recipeService.getUnitsOfMeasure().subscribe(data => {
      this.units.set(data);
    });
  }

  asFormGroup(control: AbstractControl): FormGroup {
    return control as FormGroup;
  }
}
