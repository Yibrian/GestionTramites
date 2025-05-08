<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Usuario extends Model
{
    use HasFactory;

    protected $table = 'usuarios';
    protected $primaryKey = 'id_usuario';

    protected $fillable = [
        'nombre',
        'correo',
        'contrasena',
    ];

    public function tramites()
    {
        return $this->hasMany(Tramite::class, 'id_usuario', 'id_usuario');
    }
}