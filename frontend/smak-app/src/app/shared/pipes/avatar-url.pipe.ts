import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'avatarUrl',
  standalone: true
})
export class AvatarUrlPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '/assets/images/avatar-placeholder.png';
    return `http://192.168.0.103:5127${value}`;
  }
}
