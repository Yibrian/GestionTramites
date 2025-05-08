<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use App\Models\Tramite;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Requisito>
 */
class RequisitoFactory extends Factory
{
    public function definition(): array
    {
        return [
            'descripcion_requisito' => $this->faker->sentence(),
            'id_tramite' => Tramite::factory(), 
        ];
    }
}