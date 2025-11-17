import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K extends Comparable<K>, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Utiliza o comparador fornecido para definir a organização dos elementos na árvore.
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		this.comparador = comparador;
	}

	/**
	 * Construtor da classe.
	 * O comparador padrão de ordem natural será utilizado.
	 */ 
	@SuppressWarnings("unchecked")
	public ABB() {
	    init((Comparator<K>) Comparator.naturalOrder());
	}

	/**
	 * Construtor da classe.
	 * Esse construtor cria uma nova árvore binária de busca vazia.
	 *  
	 * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
	 */
	public ABB(Comparator<K> comparador) {
	    init(comparador);
	}

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    public ABB(ABB<?, V> original, Function<V, K> funcaoChave) {
        ABB<K, V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T extends Comparable<T>> ABB<T, V> copiarArvore(No<?, V> raizArvore, Function<V, T> funcaoChave, ABB<T, V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }
    
    /**
	 * Método booleano que indica se a árvore está vazia ou não.
	 * @return
	 * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
	 * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
	 */
	public Boolean vazia() {
	    return (this.raiz == null);
	}
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {
    	comparacoes = 0;
    	inicio = System.nanoTime();
    	V procurado = pesquisar(raiz, chave);
    	termino = System.nanoTime();
    	return procurado;
	}
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
    	comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);

    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
    public int inserir(K chave, V item) {
		if (raiz == null) {
			raiz = new No<>(chave, item); // Cria a raiz se a árvore estiver vazia
			tamanho++;
			return tamanho;
		}
		raiz = inserirRecursivo(raiz, chave, item);
		return tamanho;
	}
	
	private No<K, V> inserirRecursivo(No<K, V> atual, K chave, V item) {
		if (atual == null) {
			tamanho++; // Incrementa o tamanho ao inserir um novo nó
			return new No<>(chave, item);
		}
	
		int comparacao = comparador.compare(chave, atual.getChave());
		if (comparacao < 0) {
			atual.setEsquerda(inserirRecursivo(atual.getEsquerda(), chave, item));
		} else if (comparacao > 0) {
			atual.setDireita(inserirRecursivo(atual.getDireita(), chave, item));
		} else {
			// Atualiza o valor se a chave já existir
			atual.setItem(item);
		}
	
		return atual;
    }

    @Override 
    public String toString(){
    	return percorrer();
    }

    @Override
    public String percorrer() {
    	return caminhamentoEmOrdem();
    }

    public String caminhamentoEmOrdem() {
    	StringBuilder resultado = new StringBuilder();
    caminhamentoEmOrdemRecursivo(raiz, resultado);
    return resultado.toString().trim();
	}
	private void caminhamentoEmOrdemRecursivo(No<K, V> atual, StringBuilder resultado) {
		if (atual != null) {
			caminhamentoEmOrdemRecursivo(atual.getEsquerda(), resultado);
			resultado.append(atual.getChave()).append(" ");
			caminhamentoEmOrdemRecursivo(atual.getDireita(), resultado);
		}
    }

    @Override
    /**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
     */
    public V remover(K chave) {
    	if ( raiz == null){
			return null;
		}
		No<K, V> pai = null;
		No<K, V> atual = raiz;

		while (atual!= null && !atual.getChave().equals(chave)) {
			pai = atual;
			if (chave.compareTo(atual.getChave()) < 0 ) {
				atual = atual.getEsquerda();
			}else{
				atual = atual.getDireita();
			}
		}

    	
    if (atual == null) {
        return null; // Chave não encontrada
    }

    V valorRemovido = atual.getItem();

    // Caso 1: Nó sem filhos
    if (atual.getEsquerda() == null && atual.getDireita() == null) {
        if (atual == raiz) {
            raiz = null;
        } else if (pai.getEsquerda() == atual) {
            pai.setEsquerda(null);
        } else {
            pai.setDireita(null);
        }
    }
    // Caso 2: Nó com um filho
    else if (atual.getEsquerda() == null || atual.getDireita() == null) {
        No<K, V> filho = (atual.getEsquerda() != null) ? atual.getEsquerda() : atual.getDireita();
        if (atual == raiz) {
            raiz = filho;
        } else if (pai.getEsquerda() == atual) {
            pai.setEsquerda(filho);
        } else {
            pai.setDireita(filho);
        }
    }
    // Caso 3: Nó com dois filhos
    else {
        No<K, V> sucessor = obterSucessor(atual);
        K chaveSucessor = sucessor.getChave();
        V valorSucessor = sucessor.getItem();
        remover(sucessor.getChave()); // Remove o sucessor
        atual.setChave(chaveSucessor);
        atual.setItem(valorSucessor);
    }

    tamanho--; // Atualiza o tamanho da árvore
    return valorRemovido;
}

private No<K, V> obterSucessor(No<K, V> no) {
    No<K, V> atual = no.getDireita();
    while (atual != null && atual.getEsquerda() != null) {
        atual = atual.getEsquerda();
    }
    return atual;
}
    

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000;
	}
}