package sd20200915;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Espera implements SalaDeEspera {

    enum Estado {ESPERA, ATENDIDO, DESISTIU};

    private Map<String, Estado> estados = new HashMap<>();
    private ReentrantLock l = new ReentrantLock();
    private Condition c = l.newCondition();

    @Override
    public boolean espera(String nome) throws InterruptedException {
        try {
            l.lock();
            while (this.estados.get(nome) == Estado.ESPERA)
                c.await();
            return this.estados.remove(nome) == Estado.ATENDIDO;
        }finally {
            l.unlock();
        }
    }

    @Override
    public void desiste(String nome) {
        try {
            l.lock();
            if(this.estados.get(nome) == Estado.ESPERA)
                this.estados.put(nome, Estado.DESISTIU);
            c.signalAll();
        }finally {
            l.unlock();
        }
    }

    @Override
    public String atende() {
        try {
            l.lock();
            for (String nome : this.estados.keySet())
                if (this.estados.get(nome) == Estado.ESPERA) {
                    this.estados.put(nome, Estado.ATENDIDO);
                    c.signalAll();
                    return nome;
                }
            return null;
        }finally {
            l.unlock();
        }
    }
}
