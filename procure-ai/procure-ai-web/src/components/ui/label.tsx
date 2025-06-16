import { Text } from '@mantine/core';
import type { TextProps } from '@mantine/core';
import { forwardRef } from 'react';

export interface LabelProps extends Omit<TextProps, 'component'> {
  children?: React.ReactNode;
  htmlFor?: string;
}

const Label = forwardRef<HTMLLabelElement, LabelProps>(
  ({ children, htmlFor, ...props }, ref) => {
    return (
      <Text
        ref={ref}
        component="label"
        htmlFor={htmlFor}
        size="sm"
        fw={500}
        {...props}
      >
        {children}
      </Text>
    );
  }
);

Label.displayName = 'Label';

export { Label };
