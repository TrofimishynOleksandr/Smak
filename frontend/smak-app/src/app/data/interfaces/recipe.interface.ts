export interface RecipeShortDto {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  cookTimeMinutes: number;
  averageRating: number;
  ratingsCount: number;
  isFavorite: boolean;
}

export interface RecipeDetailsDto {
  id: string;
  title: string;
  description: string;
  imageUrl?: string;
  cookTimeMinutes: number;
  authorId: string;
  authorName: string;
  categoryName: string;
  ingredients: IngredientItemDto[];
  instructions: InstructionStepDto[];
  averageRating: number;
  ratingsCount: number;
  reviews: ReviewDto[];
  isFavorite: boolean;
}

export interface IngredientItemDto {
  name: string;
  quantity: number;
  unit: string;
}

export interface InstructionStepDto {
  stepNumber: number;
  description: string;
  imageUrl?: string;
}

export interface ReviewDto {
  author: string;
  authorId: string;
  avatarUrl: string;
  rating: number;
  comment: string | null;
  createdAt: string;
}

export interface CreateRecipeDto {
  title: string;
  description: string;
  cookTimeMinutes: number;
  categoryId: string;
  image?: File;
  ingredients: CreateIngredientDto[];
  instructions: CreateInstructionDto[];
}

export interface CreateIngredientDto {
  name: string;
  quantity: number | null;
  unit: number | null;
}

export interface CreateInstructionDto {
  description: string;
  image?: File;
}
