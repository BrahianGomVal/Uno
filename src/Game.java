import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;

/*
    * R=rojo
    * V=verde
    * A=azul
    * M=amarillo
    * CC=cambio de color
    * BB= cambio de color y arrastra 4
    * S=skip
    * D= arrastra 2
    * E=Reversa
 */

public class Game {
    private LinkedList<String> mDequeOfCards;
    private TreeSet<String> mFirstRival;
    private TreeSet<String> mSecondRival;
    private TreeSet<String> mThirdRival;
    private TreeSet<String> mPlayer;
    private final String[] mColores = {"Rb", "Mb", "Vb", "Ab", "Ra", "Ma", "Va", "Aa"};
    private Jugador mJugador;
    private int mNumberOfRivals;
    private GameLogic gameLogic;
    private String mPlayedCard;
    private boolean mGameOver = false;
    private LinkedList<String> mPlayedCards;
    private Vector<TreeSet> mAllPlayers;
    private int mReversado = 1;

    //constructor:
    public Game(Jugador jugador) {
        mJugador = jugador;
        mFirstRival = new TreeSet<>(new MyComparator());
        mSecondRival = new TreeSet<>(new MyComparator());
        mThirdRival = new TreeSet<>(new MyComparator());
        mPlayer = new TreeSet<>();
        mDequeOfCards = new LinkedList<>();
        gameLogic = new GameLogic();
        mPlayedCards = new LinkedList<>();
        mAllPlayers = new Vector<>();
    }

    //getters:


    public String getPlayedCard() {
        return mPlayedCard;
    }

    public boolean isGameOver() {
        return mGameOver;
    }

    public void fillCards() {
        int j = 0;
        int k = 1;
        int n = 1;
        int m = 1;
        for (int i = 0; i < 72; i++) {
            mDequeOfCards.add(k + mColores[j]);
            k++;
            j++;
            if (j == 8) {
                j = 0;
            }
            if (k == 10) {
                mDequeOfCards.add("S" + mColores[j]);
                mDequeOfCards.add("D" + mColores[j]);
                mDequeOfCards.add("E" + mColores[j]);
                if (n > 0) {
                    mDequeOfCards.add("CC" + m);
                    mDequeOfCards.add("BB" + m);
                    m++;
                }
                n *= -1;
                k = 1;
            }
        }
        Collections.shuffle(mDequeOfCards);
    }


    public void dealTheCards() {
        for (int j = 0; j < 7; j++) {
            mJugador.setMyCards(mDequeOfCards.poll());
        }
        mPlayedCard = mDequeOfCards.poll();
        mPlayedCards.add(mPlayedCard);

        for (TreeSet<String> treeSet : mAllPlayers) {
            for (int i = 0; i < 7; i++) {
                treeSet.add(mDequeOfCards.poll());
            }
        }
    }

    public void play() {
        String aux;
        int playerTurn = playerTurn();
        if (playerTurn >= 0) {
            mPlayedCard = mJugador.getMyCards().get(playerTurn);
            mPlayedCards.add(mPlayedCard);
            mJugador.getMyCards().remove(playerTurn);
            if (mPlayedCard.charAt(0) == 'E') {
                mReversado *= -1;
            }
        } else {
            mJugador.setMyCards(mDequeOfCards.poll());
        }
        if (mJugador.getMyCards().size() > 0) {
            int n = 1;
            int contador = 0;
            int reverser = 1;
            if (mReversado < 0) {
                contador = mNumberOfRivals - 1;
                reverser = -1;
                n = 3;
            }
            while (contador < 3 && contador >= 0) {
                aux = gameLogic.whatToPlay(mPlayedCard, mAllPlayers.get(contador));
                if (!aux.equals("sin carta")) {
                    mPlayedCard = aux;
                    mPlayedCards.add(mPlayedCard);
                    if (mAllPlayers.get(contador).size() == 1) {
                        System.out.println("AI" + n + " grito \"UNO\"");
                    }
                    System.out.printf("%nAI%d jugo: %s%n", n, mPlayedCard.substring(0, 2));
                    if (mAllPlayers.get(contador).isEmpty()) {
                        mGameOver = true;
                        System.out.println("AI" + n + " ha ganado");
                        break;
                    }
                    if (aux.charAt(0) == 'E') {
                        mReversado *= -1;
                        reverser *= -1;
                    }
                } else {
                    mAllPlayers.get(contador).add(mDequeOfCards.poll());
                    System.out.println("");
                    System.out.println("AI" + n + " arrastro carta");
                }
                n += reverser;
                contador += reverser;
            }
            if (mPlayedCards.size() > 10) {
                refillTheCards(mPlayedCards);
            }
        } else {
            mGameOver = true;
            System.out.println("WOOOOOOOOOOOW increible!! le has ganado a la maquina");
            System.out.println("NOTA: has ganado porque eres un genio no porque el sistema merezca menos de 5");
        }
    }

    private void refillTheCards(LinkedList<String> playedCards) {
        Iterator<String> iterator;
        Collections.shuffle(playedCards);
        iterator = playedCards.listIterator();
        while (iterator.hasNext()) {
            mDequeOfCards.add(iterator.next());
        }
        mPlayedCards.clear();
    }

    public void howManyPlayers() {
        Scanner scanner = new Scanner(System.in);
        int jugadores;
        do {
            System.out.println("Cuantos rivales?");
            jugadores = scanner.nextInt();
        } while (jugadores < 1 || jugadores > 3);
        mNumberOfRivals = jugadores;
        if (jugadores == 1) {
            mAllPlayers.add(mFirstRival);
        } else if (jugadores == 2) {
            mAllPlayers.add(mFirstRival);
            mAllPlayers.add(mSecondRival);
        } else if (jugadores == 3) {
            mAllPlayers.add(mFirstRival);
            mAllPlayers.add(mSecondRival);
            mAllPlayers.add(mThirdRival);
        }
    }

    public int playerTurn() {
        Scanner scanner = new Scanner(System.in);
        int i;
        String aux;
        boolean sameNumber;
        boolean sameLetter;
        do {
            System.out.println("Introduzca el numero de la posicion de su carta o 0 si no tiene carta o desea arrastrar");
            aux = scanner.nextLine().trim();
            if (aux.length() == 5 || aux.length() == 1) {
                i = Character.getNumericValue(aux.charAt(0)) - 1;//esto es necesario hacerlo para leer el UNO
            } else {
                i = (Character.getNumericValue(aux.charAt(0)) * 10 + Character.getNumericValue(aux.charAt(1))) - 1;
            }
            sameLetter = false;
            sameNumber = false;
            if (i != -1 && !(i >= mJugador.getMyCards().size())) {
                if (mJugador.getMyCards().size() == 2) {
                    if (aux.length() < 5) {
                        System.out.println("No dijiste \"UNO\" arrastra 2 cartas, introduce cualquier numero para continuar");
                        String garbage = scanner.next();
                        mJugador.setMyCards(mDequeOfCards.poll());
                        mJugador.setMyCards(mDequeOfCards.poll());
                    }
                }
                sameNumber = mJugador.getMyCards().get(i).charAt(0) != mPlayedCard.charAt(0);
                sameLetter = mJugador.getMyCards().get(i).charAt(1) != mPlayedCard.charAt(1);
            }
        } while (i >= mJugador.getMyCards().size() || (sameNumber && sameLetter));
        return i;
    }
}



