package sd20200110;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TrafegoAereo implements ControloTrafegoAereo{

    private final int NUM = 3;
    private boolean[] pistasOcupadas = {false};
    private ReentrantLock l = new ReentrantLock();
    private Condition c = l.newCondition();

    private int pistaLivre(){
        for(int i=0; i<NUM; i++)
            if (pistasOcupadas[i]) return i;
        return -1;
    }

    @Override
    public int pedirParaDescolar() throws InterruptedException {
        int p;
        try{
            l.lock();
            while((p = this.pistaLivre()) == -1)
                c.await();
            return p;
        }
        finally {
            l.unlock();
        }
    }

    @Override
    public int pedirParaAterrar() throws InterruptedException {
        int p;
        try{
            l.lock();
            while((p = this.pistaLivre()) == -1)
                c.await();
            return p;
        }
        finally {
            l.unlock();
        }
    }

    @Override
    public void descolou(int pista) {
        try {
            l.lock();
            this.pistasOcupadas[pista] = false;
            c.signalAll();
        }finally {
            l.unlock();
        }
    }

    @Override
    public void aterrou(int pista) {
        try {
            l.lock();
            this.pistasOcupadas[pista] = false;
            c.signal();
        }finally {
            l.unlock();
        }
    }
}
