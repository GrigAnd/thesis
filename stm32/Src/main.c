#include "main.h"

UART_HandleTypeDef huart1;
UART_HandleTypeDef huart2;
IWDG_HandleTypeDef hiwdg;

int main(void) {
  HAL_Init();
  SystemClock_Config();
  MX_GPIO_Init();
  MX_USART1_UART_Init();
  MX_USART2_UART_Init();
  MX_IWDG_Init();

  uint8_t len, buf[128];

  while (1) {
    HAL_IWDG_Refresh(&hiwdg);

    if (HAL_UART_Receive(&huart1, &len, 1, 200) != HAL_OK) continue;
    if (len == 0 || len > sizeof(buf)-1) continue;
    if (HAL_UART_Receive(&huart1, buf, len+1, 200) != HAL_OK) continue;

    uint8_t *payload = buf + 1;
    HAL_UART_Transmit(&huart2, payload, len, 500);

    uint8_t resp = 0;
    if (HAL_UART_Receive(&huart2, &resp, 1, 500) == HAL_OK && resp == 0x01) {
      HAL_GPIO_WritePin(LED_OK_GPIO_Port, LED_OK_Pin, GPIO_PIN_SET);
      HAL_GPIO_WritePin(RELAY_GPIO_Port, RELAY_Pin, GPIO_PIN_SET);
      HAL_Delay(3000);
      HAL_GPIO_WritePin(RELAY_GPIO_Port, RELAY_Pin, GPIO_PIN_RESET);
      HAL_GPIO_WritePin(LED_OK_GPIO_Port, LED_OK_Pin, GPIO_PIN_RESET);
    } else {
      HAL_GPIO_WritePin(LED_ERR_GPIO_Port, LED_ERR_Pin, GPIO_PIN_SET);
      HAL_GPIO_WritePin(BUZZER_GPIO_Port, BUZZER_Pin, GPIO_PIN_SET);
      HAL_Delay(500);
      HAL_GPIO_WritePin(BUZZER_GPIO_Port, BUZZER_Pin, GPIO_PIN_RESET);
      HAL_GPIO_WritePin(LED_ERR_GPIO_Port, LED_ERR_Pin, GPIO_PIN_RESET);
    }
  }
}
