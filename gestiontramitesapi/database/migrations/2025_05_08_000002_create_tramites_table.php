<?php
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateTramitesTable extends Migration
{
    public function up()
    {
        Schema::create('tramites', function (Blueprint $table) {
            $table->id('id_tramite');
            $table->string('nombre_tramite');
            $table->string('frecuencia');
            $table->date('fecha');
            $table->time('hora');
            $table->text('descripcion');
            $table->string('ciudad');
            $table->string('lugar');
            $table->boolean('tiene_valor');
            $table->decimal('valor_monetario', 10, 2)->nullable();
            $table->foreignId('id_usuario')
                ->constrained('usuarios', 'id_usuario')
                ->onDelete('cascade')
                ->onUpdate('cascade');
            $table->foreignId('id_tipo_tramite')
                ->constrained('tipo_tramites', 'id_tipo_tramite')
                ->onDelete('cascade')
                ->onUpdate('cascade');

            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('tramites');
    }
}