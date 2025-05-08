<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\TipoTramite>
 */
class TipoTramiteFactory extends Factory
{
    public function definition(): array
    {
        return [
            'nombre_tipo' => $this->faker->word(), 
        ];
    }
}