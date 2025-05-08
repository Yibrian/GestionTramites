<?php
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateRequisitosTable extends Migration
{
    public function up()
    {
        Schema::create('requisitos', function (Blueprint $table) {
            $table->id('id_requisito');
            $table->text('descripcion_requisito');
            $table->foreignId('id_tramite')
                ->constrained('tramites', 'id_tramite')
                ->onDelete('cascade')
                ->onUpdate('cascade');

            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('requisitos');
    }
}