<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Requisito;

class RequisitosTableSeeder extends Seeder
{
    public function run()
    {
        Requisito::factory()->count(50)->create();
    }
}