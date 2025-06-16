import { Card as MantineCard, Title, Text } from '@mantine/core';
import type { CardProps } from '@mantine/core';
import { forwardRef } from 'react';

interface CustomCardProps extends CardProps {
  children?: React.ReactNode;
}

const Card = forwardRef<HTMLDivElement, CustomCardProps>(
  ({ children, ...props }, ref) => {
    return (
      <MantineCard
        ref={ref}
        shadow="sm"
        padding="lg"
        radius="md"
        withBorder
        {...props}
      >
        {children}
      </MantineCard>
    );
  }
);

Card.displayName = 'Card';

const CardHeader = forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ children, ...props }, ref) => (
    <div ref={ref} {...props} style={{ marginBottom: '16px' }}>
      {children}
    </div>
  )
);

CardHeader.displayName = 'CardHeader';

const CardTitle = forwardRef<HTMLHeadingElement, React.HTMLAttributes<HTMLHeadingElement>>(
  ({ children, ...props }, ref) => (
    <Title ref={ref} order={3} {...props}>
      {children}
    </Title>
  )
);

CardTitle.displayName = 'CardTitle';

const CardDescription = forwardRef<HTMLParagraphElement, React.HTMLAttributes<HTMLParagraphElement>>(
  ({ children, ...props }, ref) => (
    <Text ref={ref} size="sm" c="dimmed" {...props}>
      {children}
    </Text>
  )
);

CardDescription.displayName = 'CardDescription';

const CardContent = forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ children, ...props }, ref) => (
    <div ref={ref} {...props}>
      {children}
    </div>
  )
);

CardContent.displayName = 'CardContent';

const CardFooter = forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ children, ...props }, ref) => (
    <div ref={ref} {...props} style={{ marginTop: '16px', display: 'flex', alignItems: 'center' }}>
      {children}
    </div>
  )
);

CardFooter.displayName = 'CardFooter';

export { Card, CardHeader, CardFooter, CardTitle, CardDescription, CardContent };
