# IstoEProjectJavaSwing


case -> atividade categoria complementar (true){
    if(frequencia em massa){
        if(pavilhao){
            List<Detento> detentos = buscar todos os detentos por pavilhao da unidade selecionada
            var evento = salvar evento(frequenciaEmMassa = true, "PAVILHAO")
            detentos.forEach(detento){
              registrarFrequencia()
            }
        }

        if(cela){
            List<Detento> detentos = buscar todos os detentos por cela da unidade selecionada
            var evento = salvar evento(frequenciaEmMassa = true, "CELA")
            detentos.forEach(detento){
              registrarFrequencia()
            }
		}

		if(unidade){
            List<Detento> detentos = buscar todos os detentos por unidade selecionada
            var evento = salvar evento(frequenciaEmMassa = true, "UNIDADE")
            detentos.forEach(detento){
              registrarFrequencia()
            }
		}

	}		
}

registrarFrequencia(){
    evento.id(),
    data = now(),
    hora_entrada = ?,
    hora_saida = ?,
    foto_entrada = ?,
    foto_saida = ?
}

Adicionar 2 novos campos si545_evento (Boolean frequenciaEmMassa, Enum Varchar(50) tipoFrequencia)

Nome da issue: "Remicao: Realizar ajustes eventos para aceitar eventos em massa"