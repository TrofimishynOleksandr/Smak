using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace SmakApi.Migrations
{
    /// <inheritdoc />
    public partial class AddIgredientItemToIngredientRelation : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Name",
                table: "IngredientItems");

            migrationBuilder.AddColumn<Guid>(
                name: "IngredientId",
                table: "IngredientItems",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.CreateTable(
                name: "Ingredients",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    CreatedByUserId = table.Column<Guid>(type: "uuid", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Ingredients", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Ingredients_Users_CreatedByUserId",
                        column: x => x.CreatedByUserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_IngredientItems_IngredientId",
                table: "IngredientItems",
                column: "IngredientId");

            migrationBuilder.CreateIndex(
                name: "IX_Ingredients_CreatedByUserId",
                table: "Ingredients",
                column: "CreatedByUserId");

            migrationBuilder.AddForeignKey(
                name: "FK_IngredientItems_Ingredients_IngredientId",
                table: "IngredientItems",
                column: "IngredientId",
                principalTable: "Ingredients",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_IngredientItems_Ingredients_IngredientId",
                table: "IngredientItems");

            migrationBuilder.DropTable(
                name: "Ingredients");

            migrationBuilder.DropIndex(
                name: "IX_IngredientItems_IngredientId",
                table: "IngredientItems");

            migrationBuilder.DropColumn(
                name: "IngredientId",
                table: "IngredientItems");

            migrationBuilder.AddColumn<string>(
                name: "Name",
                table: "IngredientItems",
                type: "character varying(100)",
                maxLength: 100,
                nullable: false,
                defaultValue: "");
        }
    }
}
