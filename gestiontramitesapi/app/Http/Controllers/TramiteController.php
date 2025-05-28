<?php

namespace App\Http\Controllers;

use App\Models\Tramite;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Validator;

class TramiteController extends Controller
{
    
    private $rules = [
        'nombre_tramite' => 'required|string|max:255',
        'frecuencia' => 'required|string|max:50',
        'fecha' => 'required|date',
        'hora' => 'required',
        'descripcion' => 'required|string',
        'ciudad' => 'required|string|max:100',
        'lugar' => 'required|string|max:255',
        'tiene_valor' => 'required|boolean',
        'valor_monetario' => 'nullable|numeric',
        'id_usuario' => 'required|numeric|exists:usuarios,id_usuario',
        'id_tipo_tramite' => 'required|numeric|exists:tipo_tramites,id_tipo_tramite',
    ];

    private $traductionAttributes = [
        'nombre_tramite'    => 'nombre del trámite',
        'frecuencia'        => 'frecuencia',
        'fecha'             => 'fecha',
        'hora'              => 'hora',
        'descripcion'       => 'descripción',
        'ciudad'            => 'ciudad',
        'lugar'             => 'lugar',
        'tiene_valor'       => 'tiene valor',
        'valor_monetario'   => 'valor monetario',
        'id_usuario'        => 'usuario',
        'id_tipo_tramite'   => 'tipo de trámite',
    ];

    
    public function applyValidator(Request $request)
    {
        $validator = Validator::make($request->all(), $this->rules);
        $data = [];
        if ($validator->fails()) {
            $data = response()->json([
                'errors' => $validator->errors(),
                'data' => $request->all()
            ], Response::HTTP_BAD_REQUEST);
        }
        return $data;
    }

    public function index()
    {
        
        $tramites = Tramite::with(['usuario', 'tipoTramite'])->get();
        return response()->json($tramites, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $tramite = Tramite::create($request->all());
        return response()->json([
            'message' => 'Trámite creado exitosamente',
            'tramite' => $tramite
        ], Response::HTTP_CREATED);
    }

    public function show(Tramite $tramite)
    {
        
        $tramite->load(['usuario', 'tipoTramite']);
        return response()->json($tramite, Response::HTTP_OK);
    }

    public function update(Request $request, Tramite $tramite)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $tramite->update($request->all());
        return response()->json([
            'message' => 'Trámite actualizado exitosamente',
            'tramite' => $tramite
        ], Response::HTTP_OK);
    }

    public function destroy(Tramite $tramite)
    {
        $tramite->delete();
        return response()->json([
            'message' => 'Trámite eliminado exitosamente',
            'tramite' => $tramite->id_tramite
        ], Response::HTTP_OK);
    }
}