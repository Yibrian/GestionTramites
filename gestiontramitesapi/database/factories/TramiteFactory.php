<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use App\Models\Usuario;
use App\Models\TipoTramite;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Tramite>
 */
class TramiteFactory extends Factory
{
    public function definition(): array
    {
        return [
            'nombre_tramite' => $this->faker->sentence(3),
            'frecuencia' => $this->faker->randomElement(['Diario', 'Semanal', 'Mensual']),
            'fecha' => $this->faker->date(),
            'hora' => $this->faker->time(),
            'descripcion' => $this->faker->paragraph(),
            'ciudad' => $this->faker->city(),
            'lugar' => $this->faker->address(),
            'tiene_valor' => $this->faker->boolean(),
            'valor_monetario' => $this->faker->randomFloat(2, 0, 1000),
            'id_usuario' => Usuario::factory(), // Relación con Usuario
            'id_tipo_tramite' => TipoTramite::factory(), // Relación con TipoTramite
        ];
    }
}