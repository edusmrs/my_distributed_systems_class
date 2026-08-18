import concurrent.futures
import random

from util import calcular_faturamento_filial

def main():
    num_filiais = 4
    registros_por_filial = 10000
    
    dados_filiais = [
        [random.uniform(10.0, 500.0) for _ in range(registros_por_filial)] 
        for _ in range(num_filiais)
    ]

    faturamento_total_franquia = 0.0

    with concurrent.futures.ThreadPoolExecutor(max_workers=num_filiais) as executor:
        
        futures = []
        for i, dados in enumerate(dados_filiais):
            promessa_resultado = executor.submit(calcular_faturamento_filial, dados, i+1)
            futures.append(promessa_resultado)
        
        for future in concurrent.futures.as_completed(futures):

            subtotal = future.result() 
            faturamento_total_franquia += subtotal

    print(f"faturamento total da franquia: r$ {faturamento_total_franquia:.2f}")

if __name__ == "__main__":
    main()