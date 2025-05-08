<?php
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateTipoTramitesTable extends Migration
{
    public function up()
    {
        Schema::create('tipo_tramites', function (Blueprint $table) {
            $table->id('id_tipo_tramite');
            $table->string('nombre_tipo');
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('tipo_tramites');
    }
}