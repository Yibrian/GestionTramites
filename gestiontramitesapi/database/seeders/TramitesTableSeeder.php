<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Tramite;

class TramitesTableSeeder extends Seeder
{
    public function run()
    {
        Tramite::factory()->count(20)->create();
    }
}