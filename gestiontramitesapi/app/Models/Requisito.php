<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Requisito extends Model
{
    use HasFactory;

    protected $table = 'requisitos';
    protected $primaryKey = 'id_requisito';

    protected $fillable = [
        'descripcion_requisito',
        'id_tramite',
    ];

    public function tramite()
    {
        return $this->belongsTo(Tramite::class, 'id_tramite', 'id_tramite');
    }
}