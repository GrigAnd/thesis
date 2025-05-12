PROJECT = stm32_lock
CPU     = cortex-m3
MCU     = STM32F103C8T6

CC      = arm-none-eabi-gcc
OBJCOPY = arm-none-eabi-objcopy

CFLAGS  = -Os -mthumb -mcpu=$(CPU) -DUSE_HAL_DRIVER -DSTM32F103xB \
          -IInc \
          -IDrivers/STM32F1xx_HAL_Driver/Inc \
          -IDrivers/STM32F1xx_HAL_Driver/Inc/Legacy \
          -IDrivers/CMSIS/Device/ST/STM32F1xx/Include \
          -IDrivers/CMSIS/Include

LDFLAGS = -Tstm32_flash.ld -Wl,--gc-sections

SRCS = Src/main.c Src/system_stm32f1xx.c Src/stm32f1xx_it.c \
       Drivers/STM32F1xx_HAL_Driver/Src/*.c

OBJS = $(SRCS:.c=.o)

all: $(PROJECT).bin

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

$(PROJECT).elf: $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) $(LDFLAGS) -o $@

$(PROJECT).bin: $(PROJECT).elf
	$(OBJCOPY) -O binary $< $@

clean:
	rm -f $(OBJS) $(PROJECT).elf $(PROJECT).bin
