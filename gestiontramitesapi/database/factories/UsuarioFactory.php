<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Facades\Hash;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Usuario>
 */
class UsuarioFactory extends Factory
{
    public function definition(): array
    {
        return [
            'nombre' => $this->faker->name(),
            'correo' => $this->faker->unique()->safeEmail(),
            'contrasena' => Hash::make('password'), 
        ];
    }
}