<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    public function run()
    {
        $this->call([
            UsuariosTableSeeder::class,
            TipoTramitesTableSeeder::class,
            TramitesTableSeeder::class,
            RequisitosTableSeeder::class,
        ]);
    }
}
