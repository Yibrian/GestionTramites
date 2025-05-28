<?php

namespace App\Http\Controllers;

use App\Models\Requisito;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Validator;

class RequisitoController extends Controller
{
    
    private $rules = [
        'descripcion_requisito' => 'required|string|max:255',
        'id_tramite' => 'required|numeric|exists:tramites,id_tramite',
    ];

    private $traductionAttributes = [
        'descripcion_requisito' => 'descripción del requisito',
        'id_tramite'            => 'trámite',
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
        
        $requisitos = Requisito::with('tramite')->get();
        return response()->json($requisitos, Response::HTTP_OK);
    }

    
    public function store(Request $request)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $requisito = Requisito::create($request->all());
        return response()->json([
            'message' => 'Requisito creado exitosamente',
            'requisito' => $requisito
        ], Response::HTTP_CREATED);
    }

    public function show(Requisito $requisito)
    {
        
        $requisito->load('tramite');
        return response()->json($requisito, Response::HTTP_OK);
    }

    
    public function update(Request $request, Requisito $requisito)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $requisito->update($request->all());
        return response()->json([
            'message' => 'Requisito actualizado exitosamente',
            'requisito' => $requisito
        ], Response::HTTP_OK);
    }

    public function destroy(Requisito $requisito)
    {
        $requisito->delete();
        return response()->json([
            'message' => 'Requisito eliminado exitosamente',
            'requisito' => $requisito->id_requisito
        ], Response::HTTP_OK);
    }
}