import java.util.*;

public class EscalonamentoProcessos {
    private static List<Processo> gerarProcessos(int n) {
        List<Processo> processos = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= n; i++) {
            int tempoChegada = random.nextInt(10);
            int tempoServico = random.nextInt(10) + 1;
            int prioridade = random.nextInt(n) + 1;
            processos.add(new Processo(i, tempoChegada, tempoServico, prioridade));
        }
        return processos;
    }

    private static void fifo(List<Processo> processos) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int tempoAtual = 0;
        for (Processo p : processos) {
            if (tempoAtual < p.tempoChegada) {
                tempoAtual = p.tempoChegada;
            }
            p.tempoInicio = tempoAtual;
            tempoAtual += p.tempoServico;
            p.tempoFim = tempoAtual;
            p.calcularTempoEsperaRetorno();
        }
    }

    private static void sjf(List<Processo> processos) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int tempoAtual = 0;
        List<Processo> prontos = new ArrayList<>();
        Iterator<Processo> it = processos.iterator();
    
        while (!prontos.isEmpty() || it.hasNext()) {
            while (it.hasNext()) {
                Processo proximo = it.next();
                if (proximo.tempoChegada <= tempoAtual) {
                    prontos.add(proximo);
                } else {
                    it = processos.listIterator(processos.indexOf(proximo));
                    break;
                }
            }
    
            prontos.sort(Comparator.comparingInt(p -> p.tempoServico));
            
            if (!prontos.isEmpty()) {
                Processo p = prontos.remove(0);
                p.tempoInicio = tempoAtual;
                tempoAtual += p.tempoServico;
                p.tempoFim = tempoAtual;
                p.calcularTempoEsperaRetorno();
            } else {
                tempoAtual++; 
            }
        }
    }    

    private static int roundRobin(List<Processo> processos, int quantum) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int tempoAtual = 0, ociosidade = 0;
        Queue<Processo> prontos = new LinkedList<>();
        for (Processo p : processos) {
            while (p.tempoChegada > tempoAtual && !prontos.isEmpty()) {
                Processo atual = prontos.poll();
                if (atual.tempoInicio == -1) atual.tempoInicio = tempoAtual;
                if (atual.tempoRestante > quantum) {
                    atual.tempoRestante -= quantum;
                    tempoAtual += quantum;
                    prontos.offer(atual);
                } else {
                    tempoAtual += atual.tempoRestante;
                    atual.tempoRestante = 0;
                    atual.tempoFim = tempoAtual;
                    atual.calcularTempoEsperaRetorno();
                }
            }
            prontos.offer(p);
            if (prontos.isEmpty() && p.tempoChegada > tempoAtual) {
                ociosidade += p.tempoChegada - tempoAtual;
                tempoAtual = p.tempoChegada;
            }
        }
        return ociosidade;
    }

    private static void prioridade(List<Processo> processos) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int tempoAtual = 0;
        List<Processo> prontos = new ArrayList<>();
        Iterator<Processo> it = processos.iterator();
        while (!prontos.isEmpty() || it.hasNext()) {
            while (it.hasNext() && it.next().tempoChegada <= tempoAtual) {
                prontos.add(it.next());
            }
            prontos.sort(Comparator.comparingInt(p -> p.prioridade));
            if (!prontos.isEmpty()) {
                Processo p = prontos.remove(0);
                p.tempoInicio = tempoAtual;
                tempoAtual += p.tempoServico;
                p.tempoFim = tempoAtual;
                p.calcularTempoEsperaRetorno();
            } else {
                tempoAtual++;
            }
        }
    }

    private static void loteria(List<Processo> processos, int totalTickets) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int tempoAtual = 0;
        List<Processo> prontos = new ArrayList<>();
        Random random = new Random();
        Iterator<Processo> it = processos.iterator();
        while (!prontos.isEmpty() || it.hasNext()) {
            while (it.hasNext() && it.next().tempoChegada <= tempoAtual) {
                prontos.add(it.next());
            }
            if (!prontos.isEmpty()) {
                Processo escolhido = prontos.get(random.nextInt(prontos.size()));
                prontos.remove(escolhido);
                escolhido.tempoInicio = tempoAtual;
                tempoAtual += escolhido.tempoServico;
                escolhido.tempoFim = tempoAtual;
                escolhido.calcularTempoEsperaRetorno();
            } else {
                tempoAtual++;
            }
        }
    }

    private static void imprimirResultados(List<Processo> processos, String algoritmo) {
        double somaRetorno = 0, somaEspera = 0;
        System.out.println("Algoritmo: " + algoritmo);
        for (Processo p : processos) {
            System.out.println("Processo " + p.id + ": Chegada=" + p.tempoChegada + ", Serviço=" + p.tempoServico
                    + ", Início=" + p.tempoInicio + ", Fim=" + p.tempoFim + ", Retorno=" + p.tempoRetorno
                    + ", Espera=" + p.tempoEspera);
            somaRetorno += p.tempoRetorno;
            somaEspera += p.tempoEspera;
        }
        System.out.println("Média de tempo de retorno: " + (somaRetorno / processos.size()));
        System.out.println("Média de tempo de espera: " + (somaEspera / processos.size()));
    }

    public static void main(String[] args) {
        int n = 5;
        List<Processo> processosOriginais = gerarProcessos(n);

        List<Processo> processosFIFO = new ArrayList<>();
        for (Processo p : processosOriginais) {
            processosFIFO.add(new Processo(p.id, p.tempoChegada, p.tempoServico, p.prioridade));
        }
        fifo(processosFIFO);
        imprimirResultados(processosFIFO, "FIFO");
    
        List<Processo> processosSJF = new ArrayList<>();
        for (Processo p : processosOriginais) {
            processosSJF.add(new Processo(p.id, p.tempoChegada, p.tempoServico, p.prioridade));
        }
        sjf(processosSJF);
        imprimirResultados(processosSJF, "SJF");
    
        List<Processo> processosRR = new ArrayList<>();
        for (Processo p : processosOriginais) {
            processosRR.add(new Processo(p.id, p.tempoChegada, p.tempoServico, p.prioridade));
        }
        int ociosidadeRR = roundRobin(processosRR, 4);
        imprimirResultados(processosRR, "Round Robin");
        System.out.println("Tempo de ociosidade do processador: " + ociosidadeRR);
    
        List<Processo> processosPrioridade = new ArrayList<>();
        for (Processo p : processosOriginais) {
            processosPrioridade.add(new Processo(p.id, p.tempoChegada, p.tempoServico, p.prioridade));
        }
        prioridade(processosPrioridade);
        imprimirResultados(processosPrioridade, "Prioridade");

        List<Processo> processosLoteria = new ArrayList<>();
        for (Processo p : processosOriginais) {
            processosLoteria.add(new Processo(p.id, p.tempoChegada, p.tempoServico, p.prioridade));
        }
        loteria(processosLoteria, 100);
        imprimirResultados(processosLoteria, "Loteria");
    }
    
}
