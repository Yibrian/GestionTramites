<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TipoTramite extends Model
{
    use HasFactory;

    protected $table = 'tipo_tramites';
    protected $primaryKey = 'id_tipo_tramite';

    protected $fillable = [
        'nombre_tipo',
    ];

    public function tramites()
    {
        return $this->hasMany(Tramite::class, 'id_tipo_tramite', 'id_tipo_tramite');
    }
}