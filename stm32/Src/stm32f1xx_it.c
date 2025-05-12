#include "main.h"
#include "stm32f1xx_it.h"

void SysTick_Handler(void) {
  HAL_IncTick();
}

void USART1_IRQHandler(void) {
  HAL_UART_IRQHandler(&huart1);
}

void USART2_IRQHandler(void) {
  HAL_UART_IRQHandler(&huart2);
}
