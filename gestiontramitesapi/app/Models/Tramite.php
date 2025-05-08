<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Tramite extends Model
{
    use HasFactory;

    protected $table = 'tramites';
    protected $primaryKey = 'id_tramite';

    protected $fillable = [
        'nombre_tramite',
        'frecuencia',
        'fecha',
        'hora',
        'descripcion',
        'ciudad',
        'lugar',
        'tiene_valor',
        'valor_monetario',
        'id_usuario',
        'id_tipo_tramite',
    ];

    public function usuario()
    {
        return $this->belongsTo(Usuario::class, 'id_usuario', 'id_usuario');
    }

    public function tipoTramite()
    {
        return $this->belongsTo(TipoTramite::class, 'id_tipo_tramite', 'id_tipo_tramite');
    }

    public function requisitos()
    {
        return $this->hasMany(Requisito::class, 'id_tramite', 'id_tramite');
    }
}