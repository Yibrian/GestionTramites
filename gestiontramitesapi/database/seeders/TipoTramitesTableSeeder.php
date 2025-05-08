<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\TipoTramite;

class TipoTramitesTableSeeder extends Seeder
{
    public function run()
    {
        TipoTramite::factory()->count(5)->create();
    }
}