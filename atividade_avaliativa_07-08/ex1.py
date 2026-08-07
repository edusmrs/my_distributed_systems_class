import random
from concurrent.futures import ThreadPoolExecutor
from util import somar_sublista

def exercicio_1():
    numeros = [random.randint(1, 100) for _ in range(10000)]
    
    tamanho = len(numeros) // 4
    partes = [
        numeros[0 : tamanho],
        numeros[tamanho : tamanho * 2],
        numeros[tamanho * 2 : tamanho * 3],
        numeros[tamanho * 3 :]
    ]
    
    soma_total = 0
    
    with ThreadPoolExecutor(max_workers=4) as executor:
        # da pra usar .submit tbm, usando executor.submit(somar_sublista, parte) for parte in partes
        # depois coletando usando future.result() e somando tudo.
        # map facilita isso, se o argumento for igual para todos.
        resultados_parciais = executor.map(somar_sublista, partes)
        
        soma_total = sum(resultados_parciais)
        
    print(f"soma Total calculada pelas threads: {soma_total}")
    print(f"soma real (prova real): {sum(numeros)}\n")

if __name__ == "__main__":
    exercicio_1()