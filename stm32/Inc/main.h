#ifndef MAIN_H
#define MAIN_H

#include "stm32f1xx_hal.h"

#define RELAY_GPIO_Port GPIOA
#define RELAY_Pin       GPIO_PIN_5

#define LED_OK_GPIO_Port GPIOC
#define LED_OK_Pin       GPIO_PIN_13

#define LED_ERR_GPIO_Port GPIOB
#define LED_ERR_Pin       GPIO_PIN_0

#define BUZZER_GPIO_Port GPIOB
#define BUZZER_Pin       GPIO_PIN_1

extern UART_HandleTypeDef huart1;
extern UART_HandleTypeDef huart2;
extern IWDG_HandleTypeDef hiwdg;

#endif
