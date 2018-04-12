import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

//Implementação do K-means clássico com distância euclidiana

public class KMeansInt {

	private int[][] dados; // matriz de dados que o k-means receberá, cada
								// linha um documento, cada coluna uma palavra
	private int k; // quantidade k de clusters passada
	private int[][] prototipos; //
	private double[][] distanciasEuclidianas; // matriz com as distancias
												// euclidianas entre os
												// documentos e os prototipos

	private double taxaErro; // taxa de erro definada como aceita
	private int[][] matrizParticao; // matriz binaria de partição
	private double jcm;

	private int numeroLinhas; // equivale ao numero de documentos
	private int numeroDimensoes; // equivale ao numero de palavras analisadas
									// nos documentos

	private int max; // valor máximo dos números que podem ser escolhidos
						// aleatoriamente para a criação dos prototipos
	private int min; // valor mínimo dos números que podem ser escolhidos
						// aleatoriamente para a criação dos prototipos

	/*
	 * Para inicializar o processamento do k-means é necessário receber o nome
	 * do arquivo csv que contém a matriz de dados É necessário definir a
	 * quantidade k de clusters Receberemos os valores de linhas e colunas para
	 * a matriz de dados visto que são valores previamente conhecidos e assim
	 * evitamos ter que ler o arquivo duas vezes consecutivas já que o
	 * bufferedreader é para leitura sequencial
	 */

	public KMeansInt(String arquivo, int k, double taxaErro, int linhas, int colunas) {
		try {
			BufferedReader leitor = new BufferedReader(new FileReader(arquivo));

			/*
			 * Para popular a matriz dados é necessário saber o número de linhas
			 * e o número de dimensões do corpus recebido
			 */
			numeroLinhas = linhas;
			numeroDimensoes = colunas;
			jcm = 0;
			this.k = k;
			this.taxaErro = taxaErro;

			// inicialização das matrizes
			dados = new int[numeroLinhas][numeroDimensoes];
			prototipos = new int[k][numeroDimensoes];
			distanciasEuclidianas = new double[numeroLinhas][k];
			matrizParticao = new int[k][numeroLinhas];

			// Leitura do arquivo e população da matriz de dados
			// O valor máximo já será descoberto simultaneamente a população
			max = 0;
			String linha = null;
			String[] numColunas = null;
			int i = 0;
			int j = 0;
			int co = 1;
			int iteracoes = 0;
			while ((linha = leitor.readLine()) != null) {
				if (iteracoes == 0) {
					iteracoes++;
					continue;
				}
				numColunas = linha.split(",");
				while (co <= colunas) {
					dados[i][j] = Integer.parseInt(numColunas[co]);
					if (dados[i][j] > max) {
						max = dados[i][j];
					}
					j++;
					co++;
					
				}
				j = 0;
				co = 1;
				i++;
				iteracoes++;
			}
			
			leitor.close();
			
		} catch (Exception e) {

		}
	}

	// definir aleatoriamente os prototipos
	public int [][] definirPrototipos() {
		int numAleatorio = 0;
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			/*
			 * preenche a matriz com numeros aleatorios com o max e min
			 * definidos conforme a determinada representação pós
			 * pré-processamento recebida: binaria, tf ou tfidf
			 */
			for (int j = 0; j < numeroDimensoes; j++) {
				numAleatorio = rand.nextInt(max - min + 1) + min;
				prototipos[i][j] = numAleatorio;
			}
		}
		return prototipos;
	}

	// define a distância euclidiana de cada ponto com cada centroide para
	// preencher a tabela de distancias
	public void definirDistanciasEuclidianas() {
		double soma = 0;
		double diferencaQuadrado = 0;
		double distanciaEuclidiana = 0;
		int atual = 0;
		double xi = 0;
		double xj = 0;
		// percorrendo prototipos
		for (int j = 0; j < k; j++) {
			// percorrendo documentos
			// todos para cada prototipo
			for (int i = 0; i < numeroLinhas; i++) {
				// calculo da distancia para um documento
				while (atual < numeroDimensoes) {
					xi = dados[i][atual];
					xj = prototipos[j][atual];
					diferencaQuadrado = (xi - xj) * (xi - xj);
					soma = soma + diferencaQuadrado;
					atual++;
				}
				distanciaEuclidiana = Math.sqrt(Math.abs(soma));
				distanciasEuclidianas[i][j] = distanciaEuclidiana;
				atual = 0;
				soma = 0;
			}
		}
	}

	public void clustering() {
		inicializarMatrizParticao();
		double menorDistancia = 0;
		double atual = 0;
		int cluster = 0;
		for (int j = 0; j < numeroLinhas; j++) {
			/*
			 *  Calcula qual o prototipo com menor distancia e assim define um
			 *  cluster para o dado
			 */
			for (int i = 0; i < k; i++) {
				atual = distanciasEuclidianas[j][i];
				if (i == 0) {
					menorDistancia = atual;
				}
				if (atual < menorDistancia) {
					menorDistancia = atual;
					cluster = i;
				}
			}
			// adiciona o valor um a matriz de particao no local marcando o
			// cluster a qual pertence
			matrizParticao[cluster][j] = 1;
			cluster = 0;
		}

	}

	// zera todos os campos da matriz de partição
	private void inicializarMatrizParticao() {

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < numeroLinhas; j++) {
				matrizParticao[i][j] = 0;
			}
		}

	}

	// calcula o jcm
	public double calcularJCM() {
		double jcmAtual = 0;
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < numeroLinhas; j++) {

				jcmAtual = jcmAtual
						+ (matrizParticao[i][j] * (distanciasEuclidianas[j][i] * distanciasEuclidianas[j][i]));
			}
		}
		return jcmAtual;
	}

	// atualizar os prototipos de modo a melhorar o agrupamento
	public int [][] redefinirPrototipos() {
		inicializarMatrizPrototipos();
		int integrantes = 0; 
		int dimensao = 0;
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < numeroLinhas; j++) {
				if (matrizParticao[i][j] == 1) {
					integrantes++;
					while (dimensao < numeroDimensoes) {
						prototipos[i][dimensao] = prototipos[i][dimensao] + dados[j][dimensao];
						dimensao++;
					}
					
					dimensao = 0;
				}
				
			}
			for (int w = 0; w < numeroDimensoes; w++) {
				// evitar erro aritimético de divisão por 0
				if(integrantes>0){
					prototipos[i][w] = (prototipos[i][w]/integrantes);
				}
			}
			integrantes = 0;
		}
		return prototipos;
	}

	// zerar matriz de prototipos
	private void inicializarMatrizPrototipos() {

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < numeroDimensoes; j++) {
				prototipos[i][j] = 0;
			}
		}

	}
	
	/*
	 * calcular o determinante da matriz resultante da subtração dos 
	 * prototipos da iteração passada com os da iteração atual
	 */
	public double diferencaPrototipos(int [][] prototiposAnterior) {
		double resposta = 0;
		double soma = 0;
		for(int i = 0; i<k; i++){
			for(int j = 0; j<numeroDimensoes; j++){
				soma = soma + (prototipos[i][j] - prototiposAnterior[i][j]) * (prototipos[i][j] - prototiposAnterior[i][j]);
			}
			resposta = resposta + Math.sqrt(Math.abs(soma));
			soma = 0;
		}
		resposta = resposta/k;
		return resposta;
	}
	
	// getter e setter do jcm
	public double getJCM() {
		return jcm;
	}

	public void setJCM(double jcm) {
		this.jcm = jcm;
	}

	// getter e setter da taxa de erro definida
	public double getTaxaErro() {
		return taxaErro;
	}

	public void setTaxaErro(double taxaErro) {
		this.taxaErro = taxaErro;
	}

	// PARA TESTE
	public int[][] getMatrizParticao() {
		return matrizParticao;
	}

	// getter e setter dos centroides atuais
	public final int[][] getPrototipos() {
		return prototipos;
	}

	public final void setPrototipos(int[][] prototipos) {
		this.prototipos = prototipos;
	}

}
