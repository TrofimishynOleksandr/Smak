import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'stepImageUrl',
  standalone: true
})
export class StepImageUrlPipe implements PipeTransform {
  transform(value: string | null | undefined): string | null {
    return value ? `http://localhost:5127${value}` : null;
  }
}
