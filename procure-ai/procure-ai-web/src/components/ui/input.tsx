import { TextInput } from '@mantine/core';
import type { TextInputProps } from '@mantine/core';
import { forwardRef } from 'react';

export interface InputProps extends TextInputProps {}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ ...props }, ref) => {
    return (
      <TextInput
        ref={ref}
        {...props}
      />
    );
  }
);

Input.displayName = 'Input';

export { Input };
