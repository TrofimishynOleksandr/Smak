import { Routes } from '@angular/router';
import {LayoutComponent} from './layout/layout.component';
import {HomePageComponent} from './pages/home-page/home-page.component';
import {RecipeDetailsPageComponent} from './pages/recipe-details-page/recipe-details-page.component';
import {MyRecipesPageComponent} from './pages/my-recipes-page/my-recipes-page.component';
import {RoleGuard} from './auth/role.guard';
import {RecipeCreatePageComponent} from './pages/recipe-create-page/recipe-create-page.component';
import {FavoritesPageComponent} from './pages/favorites-page/favorites-page.component';
import {AuthGuard} from './auth/auth.guard';
import {MyProfilePageComponent} from './pages/my-profile-page/my-profile-page.component';
import {ChefProfilePageComponent} from './pages/chef-profile-page/chef-profile-page.component';
import {AdminPanelComponent} from './pages/admin-panel/admin-panel.component';
import {RecipeListPageComponent} from './pages/recipe-list-page/recipe-list-page.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', component: HomePageComponent },
      { path: 'recipes', component: RecipeListPageComponent },
      { path: 'recipes/:id', component: RecipeDetailsPageComponent },

      { path: 'me', component: MyProfilePageComponent, canActivate: [AuthGuard] },
      { path: 'chef/:id', component: ChefProfilePageComponent },

      { path: 'favorites', component: FavoritesPageComponent, canActivate: [AuthGuard] },
      { path: 'my-recipes', component: MyRecipesPageComponent, canActivate: [RoleGuard], data: { roles: ['Chef'] } },
      { path: 'my-recipes/create', component: RecipeCreatePageComponent, canActivate: [RoleGuard], data: { roles: ['Chef'] } },

      {
        path: 'admin',
        component: AdminPanelComponent,
        canActivate: [RoleGuard],
        data: { roles: ['Admin'] }
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
