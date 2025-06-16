import { Button as MantineButton } from '@mantine/core';
import type { ButtonProps as MantineButtonProps } from '@mantine/core';
import { forwardRef } from 'react';

// Extend Mantine button props with custom variants
export interface CustomButtonProps extends MantineButtonProps {
  variant?: 'filled' | 'light' | 'outline' | 'subtle' | 'default' | 'gradient' | 'destructive';
  fullWidth?: boolean;
}

const Button = forwardRef<HTMLButtonElement, CustomButtonProps>(
  ({ variant = 'filled', fullWidth, ...props }, ref) => {
    // Map destructive variant to Mantine's equivalent
    const mantineVariant = variant === 'destructive' ? 'filled' : variant;
    const color = variant === 'destructive' ? 'red' : props.color;
    
    return (
      <MantineButton
        ref={ref}
        variant={mantineVariant as any}
        color={color}
        fullWidth={fullWidth}
        {...props}
      />
    );
  }
);

Button.displayName = 'Button';

export { Button };
