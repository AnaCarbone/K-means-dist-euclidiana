import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

//Implementação do K-means clássico com distância euclidiana

public class KMeans {

	private int [][] dados; //matriz de dados que o k-means receberá, cada linha um documento, cada coluna uma palavra
	private int k; //quantidade k de clusters passada
	private int [][] prototipos; //
	private double [][] distanciasEuclidianas; //matriz com as distancias euclidianas entre os documentos e os prototipos
	
	private double taxaErro; // taxa de erro definada como aceita
	private int[][] matrizParticao; // matriz binaria de partição
	private double jcm;

	private int numeroLinhas; // equivale ao numero de documentos
	private int numeroDimensoes; // equivale ao numero de palavras analisadas nos documentos
	
	private int max; // valor máximo dos números que podem ser escolhidos aleatoriamente para a criação dos prototipos
	private int min; // valor mínimo dos números que podem ser escolhidos aleatoriamente para a criação dos prototipos
	
	
	// Para inicializar o processamento do k-means é necessário receber o nome do arquivo csv que contém a matriz de dados
	// É necessário definir a quantidade k de clusters
	public KMeans(String arquivo, int k, double taxaErro)
	{
		try
		{
			BufferedReader leitor = new BufferedReader(new FileReader(arquivo));
			
			/* Para popular a matriz dados é necessário saber o número de linhas e 
			o número de dimensões do corpus recebido*/
			numeroLinhas = 1;
			numeroDimensoes = 1;
			jcm = 0;
			this.k = k;
			this.taxaErro = taxaErro;

			
			dados = new int[numeroLinhas][numeroDimensoes];
			prototipos = new int[k][numeroDimensoes];
			distanciasEuclidianas = new double[numeroLinhas][k];
			matrizParticao = new int[k][numeroLinhas];
			
			// populando a matriz de dados
			max = 1;
			min = 0;
			for(int i = 0; i<numeroLinhas; i++)
			{
				for(int j = 0; j<numeroDimensoes; j++)
				{
					// PREENCHER MATRIZ DE DADOS LENDO DO ARQUIVO PASSADO
					
					/* para nao ter que percorrer a matriz uma vez extra para descobrir o valor máximo para a 
					criação aleatória dos protótipos, já será feito ambos simultaneamente*/
					if(dados[i][j] > max)
					{
						max = dados[i][j];
					}
				}
			}
			
			
		}
		catch(Exception e){
			
		}
	}
	
	//definir aleatoriamente os prototipos
	public void definirPrototipos()
	{
		int numAleatorio = 0;
		Random rand = new Random();
		for(int i = 0; i<k; i++)
		{
			/* preenche a matriz com numeros aleatorios com o max e min definidos 
			conforme a determinada representação pós pré-processamento recebida: binaria, tf ou tfidf*/
			for(int j = 0; j<numeroDimensoes; j++)
			{
				numAleatorio = rand.nextInt((max - min) + 1) + min;
				prototipos[i][j] = numAleatorio;
			}
		}
	}
	
	// define a distância euclidiana de cada ponto com cada centroide para preencher a tabela de distancias
	public void definirDistanciasEuclidianas()
	{
		double soma = 0;
		double diferencaQuadrado = 0;
		double distanciaEuclidiana = 0;
		int atual = 0;
		double xi = 0;
		double xj = 0;
		// percorrendo prototipos
		for(int j = 0; j< k ; j++)
		{
			// percorrendo documentos
			// todos para cada prototipo
			for(int i = 0; i< numeroLinhas; i++)
			{
				// calculo da distancia para um documento
				while(atual<numeroDimensoes)
				{
					xi = dados[i][atual];
					xj = prototipos[j][atual];
					diferencaQuadrado = (xi - xj)*(xi - xj);
					soma = soma + diferencaQuadrado;
					atual++;
				}
				distanciaEuclidiana = Math.sqrt(soma);
				distanciasEuclidianas[i][j] = distanciaEuclidiana;
				atual = 0;
				soma = 0;
			}
		}
	}
	
	public void clustering()
	{
		inicializarMatrizParticao();
		double distancia = 0;
		double atual = 0;
		int cluster = 0;
		for(int j = 0; j<numeroLinhas; j++)
		{
			// calcula qual o prototipo com menor distancia e assim define um cluster para o dado
			for(int i = 0; i< k; i++ )
			{
				atual = distanciasEuclidianas[j][i];
				if(i==0)
				{
					distancia = distanciasEuclidianas[j][i];
				}
				if(atual < distancia)
				{
					distancia = atual;
					cluster = i;
				}
			}
			// adiciona o valor um a matriz de particao no local marcando o cluster a qual pertence
			matrizParticao[cluster][j] = 1;
			
		}
		
	}

	// zera todos os campos da matriz de partição 
	private void inicializarMatrizParticao() {
		
		for(int i = 0; i<k; i++){
			for(int j = 0; j<numeroLinhas; j++)
			{
				matrizParticao[i][j] = 0;
			}
		}
		
	}
	
	// calcula o jcm
	public double calcularJCM()
	{
		double jcmAtual = 0;
		for(int i = 0; i<k; i++)
		{
			for(int j = 0; j<numeroLinhas; j++){
				
				jcmAtual = jcmAtual + (matrizParticao[i][j]*(distanciasEuclidianas[j][i]*distanciasEuclidianas[j][i]));
			}
		}
		return jcmAtual;
	}
	
	// atualizar os prototipos de modo a melhorar o agrupamento
	public void redefinirPrototipos()
	{
		int integrantes = 0; // para calcular o total de dados pertencente ao determinado grupo
		int dimensao = 0;
		for(int i = 0; i<k; i++)
		{
			for(int j = 0; j<numeroLinhas; j++)
			{
				if(matrizParticao[i][j] == 1)
				{
					integrantes ++;
					while (dimensao < numeroDimensoes)
					{
						prototipos[i][dimensao] = prototipos[i][dimensao] + dados[j][dimensao];
						dimensao++;
					}
					dimensao = 0;
				}
				for(int w = 0; w<numeroDimensoes; w++)
				{
					prototipos[i][w] = prototipos[i][w]/integrantes;
				}
			}
		}
	}
	
	
	// getter e setter do jcm
	public double getJCM ()
	{
		return jcm;
	}
	
	public void setJCM(double jcm)
	{
		this.jcm = jcm;
	}
	
	// getter e setter da taxa de erro definida
	public double getTaxaErro()
	{
		return taxaErro;
	}
	
	public void setTaxaErro(double taxaErro)
	{
		this.taxaErro = taxaErro;
	}
	
	
}
