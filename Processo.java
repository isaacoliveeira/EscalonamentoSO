class Processo {
    int id, tempoChegada, tempoServico, tempoRestante, tempoInicio, tempoFim, tempoEspera, tempoRetorno, prioridade;

    public Processo(int id, int tempoChegada, int tempoServico, int prioridade) {
        this.id = id;
        this.tempoChegada = tempoChegada;
        this.tempoServico = tempoServico;
        this.tempoRestante = tempoServico;
        this.tempoInicio = -1;
        this.prioridade = prioridade;
    }

    public void calcularTempoEsperaRetorno() {
        this.tempoRetorno = this.tempoFim - this.tempoChegada;
        this.tempoEspera = this.tempoRetorno - this.tempoServico;
    }
}

